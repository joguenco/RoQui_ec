namespace RoQuiApi.RoQui.Electronic.Client;

public class VersionDto
{
    public ApplicationDto? Application { get; set; }
}

public class ApplicationDto
{
    public string? Author { get; set; }
    public string? Name { get; set; }
    public string? Version { get; set; }
    public string? VersionDatabase { get; set; }
    public string? VersionJava { get; set; }
    public string? VersionOS { get; set; }
}