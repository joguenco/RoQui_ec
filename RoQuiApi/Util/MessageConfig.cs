using System.Text.Json;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Controllers;
using Microsoft.AspNetCore.Mvc.Filters;
using RoQuiApi.RoQui.Shared;

namespace RoQuiApi.Util;

public static class MessageConfig
{
    public static IServiceCollection AddMessageConfig(this IServiceCollection services)
    {
        // Cuando falla una validacion, ASP.NET responde con su propio formato y un title
        // que dice "One or more validation errors occurred", que no le sirve a roteg.
        // Aqui se devuelve el MessageDto de siempre, con el detalle de que campo esta mal
        // dentro de Errors.
        services.Configure<ApiBehaviorOptions>(options =>
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

        return services;
    }
}