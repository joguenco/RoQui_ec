namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Document.DeliveryNote.Dto;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Security;
using System.Text.Json;

[ApiController]
[Route("[controller]")]
public class DeliveryNoteController : ControllerBase
{
    private readonly IDeliveryNoteRepo _deliveryNoteRepo;
    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;
    private readonly ILogger<DeliveryNoteController> _logger;

    public DeliveryNoteController(IDeliveryNoteRepo deliveryNoteRepo, IElectronicRepo electronicRepo, IMapper mapper, ILogger<DeliveryNoteController> logger)
    {
        _deliveryNoteRepo = deliveryNoteRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
        _logger = logger;
    }

    [ApiKey]
    [HttpPost("rest/v1/deliverynote/send", Name = "CreateDeliveryNote")]
    public async Task<ActionResult<MessageDto>> CreateDeliveryNote(DeliveryNoteDto deliveryNoteBody)
    {
        try
        {
            var electronic = _electronicRepo.GetElectronicByCodeAndNumber(deliveryNoteBody.Code, deliveryNoteBody.Number);
            if (electronic?.Status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = electronic.Status });
            }

            var existingDeliveryNote = _deliveryNoteRepo.GetDeliveryNoteByCodeAndNumber(deliveryNoteBody.Code, deliveryNoteBody.Number);
            if (existingDeliveryNote != null)
            {
                _deliveryNoteRepo.DeleteDeliveryNote(existingDeliveryNote);
            }

            var deliveryNoteModel = _mapper.Map<DeliveryNote>(deliveryNoteBody);
            var receiversModel = _mapper.Map<List<DeliveryNoteReceiver>>(deliveryNoteBody.DeliveryNoteReceivers);
            deliveryNoteModel.DeliveryNoteReceivers = receiversModel;
            _deliveryNoteRepo.CreateDeliveryNote(deliveryNoteModel);
            _deliveryNoteRepo.SaveChanges();

            _ = Client<DeliveryNoteController>.Authorize("/roqui/v2/deliverynote/authorize", deliveryNoteBody.Code, deliveryNoteBody.Number, _electronicRepo, _logger);

            _logger.LogInformation("Created {code} {number}", deliveryNoteBody.Code, deliveryNoteBody.Number);
            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            _logger.LogCritical(ex, "Error: {deliveryNoteBody}", JsonSerializer.Serialize(deliveryNoteBody));
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
