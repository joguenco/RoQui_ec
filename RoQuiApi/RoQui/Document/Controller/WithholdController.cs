namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Document.Withhold.Dto;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Electronic.Client;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class WithholdController : ControllerBase
{
    private readonly IWithholdRepo _withholdRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public WithholdController(IWithholdRepo withholdRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _withholdRepo = withholdRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/withhold/send", Name = "CreateWithhold")]
    public async Task<ActionResult<MessageDto>> CreateWithhold(WithholdDto withholdBody)
    {
        try
        {
            var electronic = _electronicRepo.GetElectronicByCodeAndNumber(withholdBody.Code, withholdBody.Number);
            if (electronic?.Status == "AUTORIZADO")
            {
                return Ok(new MessageDto { Title = electronic.Status });
            }

            var existingWithhold = _withholdRepo.GetWithholdByCodeAndNumber(withholdBody.Code, withholdBody.Number);
            if (existingWithhold != null)
            {
                _withholdRepo.DeleteWithhold(existingWithhold);
            }

            var withholdModel = _mapper.Map<Model.Withhold>(withholdBody);
            var supportsModel = _mapper.Map<List<WithholdSupport>>(withholdBody.WithholdSupports);
            withholdModel.WithholdSupports = supportsModel;
            _withholdRepo.CreateWithhold(withholdModel);
            _withholdRepo.SaveChanges();

            _ = Client.Authorize("/roqui/v2/withhold/authorize", withholdBody.Code, withholdBody.Number, _electronicRepo);

            return Ok(new MessageDto { Title = "ENVIADO" });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new MessageDto { Title = "Error", Errors = new Error { Message = [ex.Message] } });
        }
    }
}