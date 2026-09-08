using RoQuiApi.Util;

namespace RoQuiApi.Data;

using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;

public static class PrepareDb
{
    public static void Prepare(IApplicationBuilder app)
    {
        using (var serviceScope = app.ApplicationServices.CreateScope())
        {
            ObjectDbCreator(serviceScope.ServiceProvider.GetService<AppDbContext>());
            SeedData(serviceScope.ServiceProvider.GetService<AppDbContext>());
        }
    }

    private static void ObjectDbCreator(AppDbContext context)
    {
        InjectObjectDb(context.Database, "database.sql");
    }

    private static void InjectObjectDb(DatabaseFacade db, string sqlFileName)
    {
        var resource = SqlUtil.SqlResource(sqlFileName);
        using var reader = new StreamReader(resource);
        var sqlDdl = reader.ReadToEnd();

        db.ExecuteSqlRaw(sqlDdl);
    }

    private static void SeedData(AppDbContext context)
    {
        
    }
}