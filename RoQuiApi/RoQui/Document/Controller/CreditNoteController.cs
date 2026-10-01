namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Security;

[ApiController]
[Route("[controller]")]
public class CreditNoteController : ControllerBase
{
    private readonly IInvoiceRepo _invoiceRepo;
    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;
    private readonly ILogger<CreditNoteController> _logger;

    public CreditNoteController(IInvoiceRepo invoiceRepo, IElectronicRepo electronicRepo, IMapper mapper, ILogger<CreditNoteController> logger)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
        _logger = logger;
    }

    [ApiKey]
    [HttpPost("rest/v1/creditnote/send", Name = "CreateCreditNote")]
    public async Task<ActionResult<MessageDto>> CreateCreditNote(CreditNoteDto creditNoteBody)
    {
        try
        {
            var electronic = _electronicRepo.GetElectronicByCodeAndNumber(creditNoteBody.Code, creditNoteBody.Number);
            if (electronic?.Status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = electronic.Status });
            }

            var existingDocument = _invoiceRepo.GetDocumentByCodeAndNumber(creditNoteBody.Code, creditNoteBody.Number);
            if (existingDocument != null)
            {
                _invoiceRepo.DeleteDocument(existingDocument);
            }

            var creditNoteModel = _mapper.Map<Document>(creditNoteBody);
            var creditNoteDetailsModel = _mapper.Map<List<DocumentDetail>>(creditNoteBody.CreditNoteDetails);
            creditNoteModel.DocumentDetails = creditNoteDetailsModel;
            // El XML de nota de credito del SRI no tiene direccion del comprador,
            // asi que Oracle no la manda, pero documents.address es NOT NULL
            creditNoteModel.Address = "";
            // La nota de credito no lleva formas de pago en el XML del SRI
            creditNoteModel.DocumentPayments = [];
            _invoiceRepo.CreateInvoice(creditNoteModel);
            _invoiceRepo.SaveChanges();

            _ = Client<CreditNoteController>.Authorize("/roqui/v2/creditnote/authorize", creditNoteBody.Code, creditNoteBody.Number, _electronicRepo, _logger);

            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
