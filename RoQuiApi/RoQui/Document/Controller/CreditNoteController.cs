namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class CreditNoteController : ControllerBase
{
    [HttpPost("rest/v1/creditnote", Name = "CreateCreditNote")]
    public ActionResult<MessageDto> CreateCreditNote(CreditNoteDto creditNoteBody)
    {
        // Pendiente: mapear a Model y guardar cuando exista ICreditNoteRepo
        return Ok(new MessageDto { Title = "Credit note validated successfully" });
    }
}
