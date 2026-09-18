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
    public async Task<ActionResult<MessageDto>> AuthorizeDocument(DocumentStatusDto document)
    {
        try
        {
            var status = _electronicRepo.GetElectronicByCodeAndNumber(document.Code, document.Number);
            return Ok(new MessageDto { Title = status });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}