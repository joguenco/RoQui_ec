namespace RoQuiApi.RoQui.Version;

using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Version.Repository;

[ApiController]
[Route("[controller]")]
public class VersionController : ControllerBase
{
    private readonly IVersionRepo versionRepo;

    public VersionController(IVersionRepo versionRepo)
    {
        this.versionRepo = versionRepo;
    }

    [HttpGet(Name = "GetVersion")]
    public VersionDto Get()
    {
        return new VersionDto
        {
            Name = "RoQui API",
            Author = "Jorge Luis",
            Release = "0.0.1",
            VersionOS = Environment.OSVersion.VersionString,
            VersionLanguage = ".NET Runtime " + Environment.Version.ToString(),
            VersionDatabase = versionRepo.GetVersion()
        };
    }
}
