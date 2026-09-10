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
        var existingDocument = _invoiceRepo.GetDocumentByCodeAndNumber(invoiceBody.Code, invoiceBody.Number);
        if (existingDocument != null)
        {
            _invoiceRepo.DeleteDocument(existingDocument);
        }

        var invoiceModel = _mapper.Map<Document>(invoiceBody);
        var invoiceDetailsModel = _mapper.Map<List<DocumentDetail>>(invoiceBody.InvoiceDetails);
        invoiceModel.DocumentDetails = invoiceDetailsModel;
        _invoiceRepo.CreateInvoice(invoiceModel);
        _invoiceRepo.SaveChanges();

        var url = _electronicRepo.GetParameterByName("RoQui HTTP Server");
        var apiKey = _electronicRepo.GetParameterByName("RoQui HTTP X-API-KEY");
        if (!string.IsNullOrWhiteSpace(url?.Value) && !string.IsNullOrWhiteSpace(apiKey?.Value))
        {
            // var authorizeUrl = $"{url.Value.TrimEnd('/')}/roqui/v1/invoice/authorize";
            var authorizeUrl = $"{url.Value.TrimEnd('/')}/roqui/v2/version";
            // _ = Client.AuthorizeInvoice(authorizeUrl, apiKey.Value, invoiceBody.Code, invoiceBody.Number);
            var version = Client.Version(authorizeUrl, apiKey.Value);
            Console.WriteLine($"Version check: {version.Result?.Application?.Name}");
        }

        return Ok(new MessageDto { Title = "Invoice created successfully" });
    }
}