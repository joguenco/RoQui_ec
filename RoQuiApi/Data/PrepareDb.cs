using RoQuiApi.Util;

namespace RoQuiApi.Data;

using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using RoQuiApi.Seed;

public static class PrepareDb
{
    public static void Prepare(IApplicationBuilder app, bool clearData)
    {
        using (var serviceScope = app.ApplicationServices.CreateScope())
        {
            var context = serviceScope.ServiceProvider.GetRequiredService<AppDbContext>();
            if (clearData)
            {
                ClearData(context.Database);
            }

            ObjectDbCreator(context);
            SeedData(context);
        }
    }

    private static void ClearData(DatabaseFacade db)
    {
        db.ExecuteSqlRaw("delete from taxpayers");
    }

    private static void ObjectDbCreator(AppDbContext context)
    {
        InjectObjectDb(context.Database, "PostgreSQL-db.sql");
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
        ParameterSeed.SeedParameters(context);
    }
}