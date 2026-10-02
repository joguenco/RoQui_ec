namespace RoQuiApi.Util;

public interface ILogger
{
    IDisposable BeginScope<TState>(TState state);
    bool IsEnabled(LogLevel logLevel);
    void Log<TState>(LogLevel logLevel, EventId eventId, TState state, Exception exception, Func<TState, Exception, string> formatter);
}