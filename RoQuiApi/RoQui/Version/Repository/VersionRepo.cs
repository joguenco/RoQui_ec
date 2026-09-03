namespace RoQuiApi.RoQui.Version.Repository;

using Microsoft.EntityFrameworkCore;
using Data;

public class VersionRepo : IVersionRepo
{
    private readonly AppDbContext _context;

    public VersionRepo(AppDbContext context)
    {
        this._context = context;
    }

    public string GetVersion()
    {
        string databaseVersion = _context.Database
        .SqlQueryRaw<string>("SELECT version() as \"Value\"").Single();

        return databaseVersion;
    }
}