using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Controllers;
using Microsoft.AspNetCore.Mvc.Filters;
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

// Cuando falla una validacion, ASP.NET responde con su propio formato y un title
// que dice "One or more validation errors occurred", que no le sirve a roteg.
// Aqui se devuelve el MessageDto de siempre, con el detalle de que campo esta mal
// dentro de Errors.
builder.Services.Configure<ApiBehaviorOptions>(options =>
{
    options.InvalidModelStateResponseFactory = context =>
    {
        var messages = context.ModelState
            .Where(entry => entry.Value?.Errors.Count > 0)
            .SelectMany(entry => entry.Value!.Errors.Select(error => error.ErrorMessage))
            .Where(message => !string.IsNullOrWhiteSpace(message))
            .Distinct()
            .ToList();

        // roteg solo guarda el title, el motivo queda aqui en el log junto con
        // el JSON que llego.
        // Con el nombre del controlador, para que caiga tambien en la carpeta
        // de su documento.
        var controller = (context.ActionDescriptor as ControllerActionDescriptor)?.ControllerTypeInfo.AsType();
        var logger = context.HttpContext.RequestServices.GetRequiredService<ILoggerFactory>()
            .CreateLogger(controller ?? typeof(Program));
        // El documento que llego, con su codigo y numero delante para buscarlo.
        var document = (context as ActionExecutingContext)?.ActionArguments.Values.FirstOrDefault();
        var code = document?.GetType().GetProperty("Code")?.GetValue(document);
        var number = document?.GetType().GetProperty("Number")?.GetValue(document);
        logger.LogWarning("Datos no válidos {code} {number} en {path}: {messages} {body}",
            code, number, context.HttpContext.Request.Path, string.Join(" | ", messages),
            JsonSerializer.Serialize(document));

        return new BadRequestObjectResult(new MessageDto
        {
            Title = "Datos no válidos",
            Errors = new Error { Message = messages }
        });
    };
});
builder.Services.AddOpenApiConfig();

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

app.UseAuthorization();

app.MapControllers();

app.Run();
