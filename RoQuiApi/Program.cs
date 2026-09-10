using Microsoft.EntityFrameworkCore;
using RoQuiApi.Data;
using RoQuiApi.Profiles;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Head.Repository;
using RoQuiApi.RoQui.Invoice.Repository;
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
    PrepareDb.Prepare(app);
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
