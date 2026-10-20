namespace RoQuiApi.Util;

using Serilog;

public static class LoggerConfig
{
    public static IHostBuilder UseLoggerConfig(this IHostBuilder host)
    {
        // Un archivo por dia, Serilog guarda los ultimos 31 y borra solo los viejos. shared para
        // que al reiniciar no se abra otro archivo con _001.
        var loggerConfiguration = new LoggerConfiguration()
            .WriteTo.Console()
            .WriteTo.File("logs/roqui_api.log", rollingInterval: RollingInterval.Day, shared: true);

        Log.Logger = loggerConfiguration.CreateLogger();
        return host.UseSerilog();
    }
}
