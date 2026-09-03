namespace RoQuiApi.RoQui.Ping;

using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("[controller]")]
public class PingController : ControllerBase
{

    [HttpGet(Name = "GetPing")]
    public Ping Get()
    {
        return new Ping { Message = "Pong" };
    }
}
