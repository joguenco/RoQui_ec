using Microsoft.EntityFrameworkCore;
using RoQuiApi.Data;
using RoQuiApi.Profiles;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Head.Repository;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Security;
using RoQuiApi.RoQui.Version.Repository;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddDbContext<AppDbContext>(
    options =>
    {
        options.UseNpgsql(builder.Configuration
            .GetConnectionString(name: "DefaultConnection"));
    });

builder.Services.AddScoped<IVersionRepo, VersionRepo>();
builder.Services.AddScoped<ITaxpayerRepo, TaxpayerRepo>();
builder.Services.AddScoped<IInvoiceRepo, InvoiceRepo>();
builder.Services.AddScoped<IElectronicRepo, ElectronicRepo>();
builder.Services.AddScoped<IWithholdRepo, WithholdRepo>();
builder.Services.AddScoped<IDeliveryNoteRepo, DeliveryNoteRepo>();

// Validacion de la X-API-KEY. Van como Scoped porque el validador pide el
// repositorio, y ese depende del DbContext.
builder.Services.AddScoped<IApiKeyValidator, ApiKeyValidator>();
builder.Services.AddScoped<ApiKeyAuthorizationFilter>();

// Added Auto Mapper
builder.Services.AddAutoMapper(cfg => { }, typeof(MappingProfile));

// builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi(options =>
{
    // Orden en que se muestran los grupos en Scalar
    string[] tagOrder =
    [
        "Ping",
        "Version",
        "Taxpayer",
        "Invoice",
        "CreditNote",
        "DebitNote",
        "Liquidation",
        "Withhold"
    ];

    options.AddDocumentTransformer((document, context, cancellationToken) =>
    {
        if (document.Tags is { Count: > 0 })
        {
            var sortedTags = document.Tags
                .OrderBy(tag =>
                {
                    var position = Array.IndexOf(tagOrder, tag.Name);
                    return position < 0 ? int.MaxValue : position;
                })
                .ThenBy(tag => tag.Name)
                .ToList();

            document.Tags.Clear();
            foreach (var tag in sortedTags)
            {
                document.Tags.Add(tag);
            }
        }

        return Task.CompletedTask;
    });
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.MapScalarApiReference();
}

// Las vistas v_ele_* y los parametros tienen que crearse tambien en produccion.
// Esto estaba dentro del if de arriba, y en IIS el entorno es Production, asi que
// la base se quedaba sin vistas y RoQui no arrancaba por el ddl-auto=validate.
PrepareDb.Prepare(app);

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
