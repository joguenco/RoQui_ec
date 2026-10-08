using System.Threading.RateLimiting;
using RoQuiApi.RoQui.Shared;

namespace RoQuiApi.Util;

// Rate limit: tope de peticiones por IP. Lo que pasa del tope responde 429 y no
// se procesa. roteg manda todo desde una sola IP (el servidor de Oracle) y cada
// documento son unas 2 peticiones, por eso el tope es alto: corta bucles y
// ataques, no el trabajo normal.
// Para cambiarlo sin recompilar, en appsettings.Production.json:
//   "RateLimit": { "PermitLimit": 50, "WindowSeconds": 60 }
// El app.UseRateLimiter() se queda en Program.cs, ahi se decide el orden.
public static class RateLimit
{
    public static IServiceCollection AddRateLimit(this IServiceCollection services, IConfiguration configuration)
    {
        var permitLimit = configuration.GetValue("RateLimit:PermitLimit", 50); // peticiones...
        var windowSeconds = configuration.GetValue("RateLimit:WindowSeconds", 60); // ...en estos segundos

        services.AddRateLimiter(options =>
        {
            // Cada IP tiene su propio contador, que vuelve a cero al acabar la ventana.
            options.GlobalLimiter = PartitionedRateLimiter.Create<HttpContext, string>(context =>
                RateLimitPartition.GetFixedWindowLimiter(
                    context.Connection.RemoteIpAddress?.ToString() ?? "sin-ip",
                    _ => new FixedWindowRateLimiterOptions
                    {
                        PermitLimit = permitLimit,
                        Window = TimeSpan.FromSeconds(windowSeconds),
                        // las que pasan del tope no esperan en cola, se rechazan
                        QueueLimit = 0,
                    }));

            // Al rechazar: 429, cuantos segundos faltan para volver a intentar, el
            // MessageDto de siempre y una linea en el log.
            options.RejectionStatusCode = StatusCodes.Status429TooManyRequests;
            options.OnRejected = async (context, cancellationToken) =>
            {
                var http = context.HttpContext;
                if (context.Lease.TryGetMetadata(MetadataName.RetryAfter, out var retryAfter))
                {
                    http.Response.Headers.RetryAfter = Math.Ceiling(retryAfter.TotalSeconds).ToString();
                }
                http.RequestServices.GetRequiredService<ILogger<Program>>().LogWarning(
                    "Demasiadas peticiones desde {ip} en {path}", http.Connection.RemoteIpAddress, http.Request.Path);
                await http.Response.WriteAsJsonAsync(new MessageDto { Title = "Demasiadas peticiones" }, cancellationToken);
            };
        });

        return services;
    }
}
