namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Repository;
using AutoMapper;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Document.Dto;

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

    [HttpPost("rest/v1/invoice/send", Name = "CreateInvoice")]
    public async Task<ActionResult<MessageDto>> CreateInvoice(InvoiceDto invoiceBody)
    {
        try
        {
            var status = _electronicRepo.GetElectronicByCodeAndNumber(invoiceBody.Code, invoiceBody.Number);
            if (status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = status });
            }

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

            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }

    [HttpPost("rest/v1/invoice/authorize", Name = "AuthorizeInvoice")]
    public async Task<ActionResult<MessageDto>> AuthorizeInvoice(DocumentStatusDto document)
    {
        try
        {
            var status = _electronicRepo.GetElectronicByCodeAndNumber(document.Code, document.Number);
            return Ok(new MessageDto { Title = status });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}