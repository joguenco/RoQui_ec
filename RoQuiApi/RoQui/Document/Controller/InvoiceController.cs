namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Repository;
using AutoMapper;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;

[ApiController]
[Route("[controller]")]
public class InvoiceController : ControllerBase
{

    private readonly IInvoiceRepo _invoiceRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public InvoiceController(IInvoiceRepo invoiceRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/invoice", Name = "CreateInvoice")]
    public async Task<ActionResult<MessageDto>> CreateInvoice(InvoiceDto invoiceBody)
    {
        try
        {
            var existingDocument = _invoiceRepo.GetDocumentByCodeAndNumber(invoiceBody.Code, invoiceBody.Number);
            if (existingDocument != null)
            {
                _invoiceRepo.DeleteDocument(existingDocument);
            }

            var invoiceModel = _mapper.Map<Document>(invoiceBody);
            var invoiceDetailsModel = _mapper.Map<List<DocumentDetail>>(invoiceBody.InvoiceDetails);
            var invoicePaymentsModel = _mapper.Map<List<DocumentPayment>>(invoiceBody.Payments);
            var invoiceInformationsModel = _mapper.Map<List<DocumentInformation>>(invoiceBody.Informations);

            invoiceModel.DocumentDetails = invoiceDetailsModel;
            invoiceModel.DocumentPayments = invoicePaymentsModel;
            invoiceModel.DocumentInformations = invoiceInformationsModel;

            _invoiceRepo.CreateInvoice(invoiceModel);
            _invoiceRepo.SaveChanges();

            _ = Client.Authorize("/roqui/v2/invoice/authorize", invoiceBody.Code, invoiceBody.Number, _electronicRepo);

            return Ok(new MessageDto { Title = "Invoice created successfully" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error creating invoice", Errors = new Error { Message = [ex.Message] } });
        }
    }
}