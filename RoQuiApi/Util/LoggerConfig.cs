namespace RoQuiApi.Util;

using RoQuiApi.RoQui.Invoice.Controller;
using Serilog;
using Serilog.Filters;

public static class LoggerConfig
{
    public static IHostBuilder UseLoggerConfig(this IHostBuilder host)
    {
        // En logs/general va todo y cada documento tiene ademas su carpeta. Un archivo
        // por dia, Serilog guarda los ultimos 31 y borra solo los viejos. shared para
        // que al reiniciar no se abra otro archivo con _001.
        var loggerConfiguration = new LoggerConfiguration()
            .WriteTo.Console()
            .WriteTo.File("logs/roqui_api.log", rollingInterval: RollingInterval.Day, shared: true);

        foreach (var (controller, folder) in new (Type, string)[]
        {
            (typeof(InvoiceController), "invoice"),
            (typeof(LiquidationController), "liquidation"),
            (typeof(CreditNoteController), "creditnote"),
            (typeof(DebitNoteController), "debitnote"),
            (typeof(DeliveryNoteController), "deliverynote"),
            (typeof(WithholdController), "withhold"),
        })
        {
            loggerConfiguration.WriteTo.Logger(document => document
                .Filter.ByIncludingOnly(Matching.FromSource(controller.FullName!))
                .WriteTo.File($"logs/{folder}/{folder}.log", rollingInterval: RollingInterval.Day, shared: true));
        }

        Log.Logger = loggerConfiguration.CreateLogger();
        return host.UseSerilog();
    }
}
