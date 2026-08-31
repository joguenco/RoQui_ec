namespace RoQuiApi.RoQui.Version.Repository;

using Microsoft.EntityFrameworkCore;
using RoQuiApi.Data;

public class VersionRepo : IVersionRepo
{
    private readonly AppDbContext context;

    public VersionRepo(AppDbContext context)
    {
        this.context = context;
    }

    public string GetVersion()
    {
        string databaseVersion = context.Database
        .SqlQueryRaw<string>("SELECT version() as \"Value\"").Single();

        return databaseVersion;
    }
}