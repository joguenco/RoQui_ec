namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class DebitNoteController : ControllerBase
{
    [HttpPost("rest/v1/debitnote/send", Name = "CreateDebitNote")]
    public ActionResult<MessageDto> CreateDebitNote(DebitNoteDto debitNoteBody)
    {
        // Pendiente: mapear a Model y guardar cuando exista IDebitNoteRepo.
        // Mientras tanto devuelve un estado corto y honesto: el titulo se guarda
        // en ele_documentos_electronicos.estado, que es VARCHAR2(20) en Oracle
        return Ok(new MessageDto { Title = "NO IMPLEMENTADO" });
    }
}
