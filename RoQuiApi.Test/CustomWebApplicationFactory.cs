using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.Extensions.Configuration;

public class CustomWebApplicationFactory<TProgram> : WebApplicationFactory<TProgram> where TProgram : class
{
    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        builder.UseEnvironment("Testing"); // Forces the API to boot in a "Testing" environment context

        builder.ConfigureAppConfiguration((context, config) =>
        {
            // Optional: Explicitly force or seed specific test parameters here if needed
        });
    }
}
