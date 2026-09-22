namespace RoQuiApi.RoQui.Electronic.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class DocumentController : ControllerBase
{
    private readonly IElectronicRepo _electronicRepo;

    public DocumentController(IElectronicRepo electronicRepo)
    {
        _electronicRepo = electronicRepo;
    }

    [HttpPost("rest/v1/document/authorize", Name = "AuthorizeDocument")]
    public ActionResult<DocumentStatusDto> AuthorizeDocument(DocumentStatusDto document)
    {
        try
        {
            var electronic = _electronicRepo.GetElectronicByCodeAndNumber(document.Code, document.Number);

            // Oracle necesita los cuatro datos por separado para meterlos en su columna,
            // no solo el estado.
            return Ok(new DocumentStatusDto
            {
                Code = document.Code,
                Number = document.Number,
                AuthorizationCode = electronic?.AuthorizationCode,
                AuthorizationDate = electronic?.AuthorizationDate,
                Observation = electronic?.Observation,
                Status = electronic?.Status ?? "NO ENVIADO"
            });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
