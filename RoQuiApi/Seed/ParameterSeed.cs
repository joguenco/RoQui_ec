using RoQuiApi.Data;
using RoQuiApi.RoQui.Electronic.Model;

namespace RoQuiApi.Seed;

public class ParameterSeed
{
    public static void SeedParameters(AppDbContext context)
    {
        if (!context.Parameters.Any())
        {
            var parameters = new List<Parameter>
            {
                new() { Id = 1, Name = "Base Directory", Value = "/app/RoQui", Observation = "Base directory for files", Type = "SRI" },
                new() { Id = 2, Name = "Certificate", Value = "Certificate.p12", Observation = "Certificate name", Type = "Certificate" },
                new() { Id = 3, Name = "Certificate Password", Value = "***************==", Observation = "Certificate Password", Type = "Certificate" },
                new() { Id = 4, Name = "Logo JPEG", Value = "logo.jpeg", Observation = "URL logo JPEG", Type = "SRI" },
                new() { Id = 5, Name = "Email SMTP Server", Value = "localhost", Observation = "Email SMTP Server", Type = "Email SMTP" },
                new() { Id = 6, Name = "Port Email SMTP Server", Value = "1025", Observation = "Port Email SMTP Server", Type = "Email SMTP" },
                new() { Id = 7, Name = "Email Account", Value = "hola@localhost", Observation = "Account of Email SMTP Server", Type = "Email SMTP" },
                new() { Id = 8, Name = "Email Password Account", Value = "", Observation = "Password Account of Email SMTP Server", Type = "Email SMTP" },
                new() { Id = 9, Name = "Email Encryption", Value = "None", Observation = "Connection Encryption: None, SSL/TLS ", Type = "Email SMTP" },
                new() { Id = 10, Name = "Email HTTP Server", Value = "https://mail.server.com", Observation = "Email HTTP Server", Type = "Email HTTP" },
                new() { Id = 11, Name = "Email HTTP Server Token", Value = "**************==", Observation = "Token Email HTTP Server", Type = "Email HTTP" },
                new() { Id = 12, Name = "Logo PNG", Value = "logo.png", Observation = "URL Logo PNG", Type = "Resource" },
                new() { Id = 13, Name = "Template Email", Value = "template.html", Observation = "URL template", Type = "Resource" },
                new() { Id = 14, Name = "RoQui HTTP Server", Value = "http://localhost:8080", Observation = "Server for electronic documents authorization", Type = "Resource" },
                new() { Id = 99, Name = "Subscription", Value = "w1b5fZ31Z+8qnlYW0Sa3vA==", Observation = "Subscription", Type = "Subscription" }
            };

            context.Parameters.AddRange(parameters);
            context.SaveChanges();
        }
    }
}
