namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Document.DeliveryNote.Dto;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class DeliveryNoteController : ControllerBase
{
    private readonly IDeliveryNoteRepo _deliveryNoteRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public DeliveryNoteController(IDeliveryNoteRepo deliveryNoteRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _deliveryNoteRepo = deliveryNoteRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/deliverynote/send", Name = "CreateDeliveryNote")]
    public async Task<ActionResult<MessageDto>> CreateDeliveryNote(DeliveryNoteDto deliveryNoteBody)
    {
        try
        {
            var status = _electronicRepo.GetElectronicByCodeAndNumber(deliveryNoteBody.Code, deliveryNoteBody.Number);
            if (status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = status });
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

            _ = Client.Authorize("/roqui/v2/deliverynote/authorize", deliveryNoteBody.Code, deliveryNoteBody.Number, _electronicRepo);

            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}
