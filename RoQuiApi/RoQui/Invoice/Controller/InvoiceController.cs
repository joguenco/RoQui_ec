namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Repository;
using AutoMapper;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Invoice.Model;

[ApiController]
[Route("[controller]")]
public class InvoiceController : ControllerBase
{
    private readonly IInvoiceRepo _invoiceRepo;
    private readonly IMapper _mapper;

    public InvoiceController(IInvoiceRepo invoiceRepo, IMapper mapper)
    {
        _invoiceRepo = invoiceRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/invoice", Name = "CreateInvoice")]
    public ActionResult<MessageDto> CreateInvoice(InvoiceDto invoiceBody)
    {
        var invoiceModel = _mapper.Map<Invoice>(invoiceBody);
        _invoiceRepo.CreateInvoice(invoiceModel);
        _invoiceRepo.SaveChanges();

        return Ok(new MessageDto { Title = "Invoice created successfully" });
    }
}