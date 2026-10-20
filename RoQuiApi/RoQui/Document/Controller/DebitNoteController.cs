namespace RoQuiApi.RoQui.Document.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.Profiles;
using RoQuiApi.RoQui.Document.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Security;
using System.Text.Json;
using RoQuiApi.RoQui.Document.Dto;

[ApiController]
[Route("[controller]")]
public class DebitNoteController : ControllerBase
{
    private readonly IDocumentRepo _invoiceRepo;
    private readonly IElectronicRepo _electronicRepo;
    private readonly RoQuiMapper _mapper;
    private readonly ILogger<DebitNoteController> _logger;

    public DebitNoteController(IDocumentRepo invoiceRepo, IElectronicRepo electronicRepo, RoQuiMapper mapper, ILogger<DebitNoteController> logger)
    {
        _invoiceRepo = invoiceRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
        _logger = logger;
    }

    [ApiKey]
    [HttpPost("rest/v1/send", Name = "CreateDebitNote")]
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

            var debitNoteModel = _mapper.ToDebitNoteDocument(debitNoteBody);

            // El dto no trae numero de linea, se lo pongo yo en el orden que vino
            var line = 0;
            debitNoteModel.DebitNoteReasons = debitNoteBody.DebitNoteDetails
                .Select(detail =>
                {
                    var reason = _mapper.ToDebitNoteReason(detail);
                    reason.Line = ++line;
                    return reason;
                })
                .ToList();

            debitNoteModel.DebitNoteTaxes = _mapper.ToDebitNoteTaxes(debitNoteBody.DebitNoteTaxes);

            // El XML de nota de debito del SRI no lleva direccion del comprador,
            // pero documents.address es NOT NULL
            debitNoteModel.Address = "";
            // Los motivos no son lineas de detalle, van en debit_notes_reason
            debitNoteModel.DocumentDetails = [];
            debitNoteModel.DocumentPayments =
                _mapper.ToDocumentPayments(debitNoteBody.Payments ?? []);
            _invoiceRepo.CreateInvoice(debitNoteModel);
            _invoiceRepo.SaveChanges();

            _ = Client<DebitNoteController>.Authorize("/roqui/v2/debitnote/authorize", debitNoteBody.Code, debitNoteBody.Number, _electronicRepo, _logger);

            _logger.LogInformation("Created {code} {number}", debitNoteBody.Code, debitNoteBody.Number);
            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            _logger.LogCritical(ex, "Error: {debitNoteBody}", JsonSerializer.Serialize(debitNoteBody));
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
