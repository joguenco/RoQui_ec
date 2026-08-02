namespace RoQui.Version;

using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("[controller]")]
public class VersionController : ControllerBase
{

    [HttpGet(Name = "GetVersion")]
    public Version Get()
    {
        return new Version { Number = "1.0.0" };
    }
}
