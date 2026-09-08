namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class DebitNoteController : ControllerBase
{
    [HttpPost("rest/v1/debitnote", Name = "CreateDebitNote")]
    public ActionResult<MessageDto> CreateDebitNote(DebitNoteDto debitNoteBody)
    {
        // Pendiente: mapear a Model y guardar cuando exista IDebitNoteRepo
        return Ok(new MessageDto { Title = "Debit note validated successfully" });
    }
}
