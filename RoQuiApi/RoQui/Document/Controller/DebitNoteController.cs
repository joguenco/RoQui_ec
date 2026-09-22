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
public class DebitNoteController : ControllerBase
{
    private readonly IInvoiceRepo _invoiceRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public DebitNoteController(IInvoiceRepo invoiceRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/debitnote/send", Name = "CreateDebitNote")]
    public async Task<ActionResult<MessageDto>> CreateDebitNote(DebitNoteDto debitNoteBody)
    {
        try
        {
            var electronic = _electronicRepo.GetElectronicByCodeAndNumber(debitNoteBody.Code, debitNoteBody.Number);
            if (electronic?.Status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = electronic.Status });
            }

            var existingDocument = _invoiceRepo.GetDocumentByCodeAndNumber(debitNoteBody.Code, debitNoteBody.Number);
            if (existingDocument != null)
            {
                _invoiceRepo.DeleteDocument(existingDocument);
            }

            var debitNoteModel = _mapper.Map<Document>(debitNoteBody);

            // El dto no trae numero de linea, se lo pongo yo en el orden que vino
            var line = 0;
            debitNoteModel.DebitNoteReasons = debitNoteBody.DebitNoteDetails
                .Select(detail =>
                {
                    var reason = _mapper.Map<DebitNoteReason>(detail);
                    reason.Line = ++line;
                    return reason;
                })
                .ToList();

            debitNoteModel.DebitNoteTaxes = _mapper.Map<List<DebitNoteTax>>(debitNoteBody.DebitNoteTaxes);

            // El XML de nota de debito del SRI no lleva direccion del comprador,
            // pero documents.address es NOT NULL
            debitNoteModel.Address = "";
            // Los motivos no son lineas de detalle, van en debit_notes_reason
            debitNoteModel.DocumentDetails = [];
            debitNoteModel.DocumentPayments =
                _mapper.Map<List<DocumentPayment>>(debitNoteBody.Payments ?? []);
            _invoiceRepo.CreateInvoice(debitNoteModel);
            _invoiceRepo.SaveChanges();

            _ = Client.Authorize("/roqui/v2/debitnote/authorize", debitNoteBody.Code, debitNoteBody.Number, _electronicRepo);

            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
