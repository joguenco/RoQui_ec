using Microsoft.EntityFrameworkCore;
using RoQuiApi.Data;
using RoQuiApi.Profiles;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Head.Repository;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Security;
using RoQuiApi.RoQui.Shared;
using RoQuiApi.RoQui.Version.Repository;
using RoQuiApi.Util;
using Scalar.AspNetCore;

var builder = WebApplication.CreateBuilder(args);
builder.Host.UseLoggerConfig();

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

builder.Services.AddMessageConfig();
builder.Services.AddOpenApiConfig();
builder.Services.AddRateLimit(builder.Configuration);

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
PrepareDb.Prepare(app, clearData: app.Environment.IsEnvironment("Testing"));

app.UseHttpsRedirection();

app.UseRateLimiter();

app.UseAuthorization();

app.MapControllers();

app.Run();
