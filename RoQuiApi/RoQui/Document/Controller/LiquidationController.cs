namespace RoQuiApi.RoQui.Invoice.Controller;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class LiquidationController : ControllerBase
{
    [HttpPost("rest/v1/liquidation", Name = "CreateLiquidation")]
    public ActionResult<MessageDto> CreateLiquidation(LiquidationDto liquidationBody)
    {
        // Pendiente: mapear a Model y guardar cuando exista ILiquidationRepo
        return Ok(new MessageDto { Title = "Liquidation validated successfully" });
    }
}
