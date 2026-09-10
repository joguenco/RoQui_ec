namespace RoQuiApi.RoQui.Invoice.Controller;

using System.Net.Http.Json;
using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Repository;
using AutoMapper;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Electronic.Repository;

[ApiController]
[Route("[controller]")]
public class InvoiceController : ControllerBase
{
    private static readonly HttpClient HttpClient = new();

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
        if (!string.IsNullOrWhiteSpace(url?.Value))
        {
            var authorizeUrl = $"{url.Value.TrimEnd('/')}/roqui/v1/invoice/authorize";
            _ = AuthorizeInvoice(authorizeUrl, invoiceBody.Code, invoiceBody.Number);
        }

        return Ok(new MessageDto { Title = "Invoice created successfully" });
    }

    private static async Task AuthorizeInvoice(string authorizeUrl, string code, string number)
    {
        try
        {
            await Task.Delay(6000);
            using var content = JsonContent.Create(new { code, number });
            var response = await HttpClient.PostAsync(authorizeUrl, content);
            Console.WriteLine($"Authorize invoice {code}-{number}: {(int)response.StatusCode}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error authorizing invoice {code}-{number}: {ex.Message}");
        }
    }
}