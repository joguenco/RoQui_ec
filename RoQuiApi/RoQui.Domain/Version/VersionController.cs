namespace RoQui.Version;

using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("[controller]")]
public class VersionController : ControllerBase
{

    [HttpGet(Name = "GetVersion")]
    public Version Get()
    {
        return new Version
        {
            Name = "RoQui API",
            Author = "Jorge Luis",
            Release = "0.0.1",
            VersionOS = Environment.OSVersion.VersionString,
            versionLanguage = ".NET Runtime " + Environment.Version.ToString(),
            versionDatabase = ""
        };
    }
}
