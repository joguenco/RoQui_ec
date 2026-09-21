namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class CreditNoteController : ControllerBase
{
    private readonly IInvoiceRepo _invoiceRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public CreditNoteController(IInvoiceRepo invoiceRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/creditnote/send", Name = "CreateCreditNote")]
    public async Task<ActionResult<MessageDto>> CreateCreditNote(CreditNoteDto creditNoteBody)
    {
        try
        {
            var status = _electronicRepo.GetElectronicByCodeAndNumber(creditNoteBody.Code, creditNoteBody.Number);
            if (status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = status });
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

            _ = Client.Authorize("/roqui/v2/creditnote/authorize", creditNoteBody.Code, creditNoteBody.Number, _electronicRepo);

            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
