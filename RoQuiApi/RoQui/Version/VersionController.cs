namespace RoQuiApi.RoQui.Version;

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ApplicationParts;
using Microsoft.AspNetCore.Mvc.Controllers;
using RoQuiApi.RoQui.Version.Repository;

[ApiController]
[Route("[controller]")]
public class VersionController : ControllerBase
{
    private readonly IVersionRepo _versionRepo;

    public VersionController(IVersionRepo versionRepo)
    {
        _versionRepo = versionRepo;
    }

    // Devuelve las versiones del sistema operativo, del runtime y de la base,
    // asi que no puede quedar abierto a cualquiera.
    [HttpGet(Name = "GetVersion")]
    public VersionDto Get()
    {
        return new VersionDto
        {
            Name = "RoQui API",
            Author = "Jorge Luis",
            Release = "0.9.0",
            VersionOS = Environment.OSVersion.VersionString,
            VersionLanguage = ".NET Runtime " + Environment.Version.ToString(),
            VersionDatabase = _versionRepo.GetVersion()
        };
    }
}

internal sealed class VersionControllerFeatureProvider : IApplicationFeatureProvider<ControllerFeature>
{
    public void PopulateFeature(
        IEnumerable<ApplicationPart> parts,
        ControllerFeature feature
    )
    {
        var controllers = feature.Controllers
            .Where(type => type.AsType() == typeof(VersionController))
            .ToList();

        foreach (var controller in controllers)
        {
            feature.Controllers.Remove(controller);
        }
    }
}
