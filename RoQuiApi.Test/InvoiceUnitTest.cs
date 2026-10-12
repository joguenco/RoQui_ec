namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Head.Dto;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

public class InvoiceUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;
    private readonly string X_API_KEY = "api__6tpXYCxsXpCs7QeuI44KtoCq";


    public InvoiceUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static TaxpayerDto InitData()
    {
        return new TaxpayerDto()
        {
            Identification = "0123456789001",
            LegalName = "Test Legal Name",
            ForcedAccounting = "SI",
            SpecialTaxpayer = "12345",
            RetentionAgent = "1",
            Establishments =
            [
                new EstablishmentDto
                {
                    Code = "001",
                    BusinessName = "Main Establishment",
                    Address = "123 Main St",
                    IsPrincipal = true
                },
                new EstablishmentDto
                {
                    Code = "002",
                    BusinessName = "Secondary Establishment",
                    Address = "456 Secondary St",
                    IsPrincipal = false
                },
                new EstablishmentDto
                {
                    Code = "003",
                    BusinessName = "Tertiary Establishment",
                    Address = "789 Tertiary St",
                    IsPrincipal = false
                }
            ]
        };
    }

    public static InvoiceDto InitInvoiceData()
    {
        return new InvoiceDto
        {
            Code = "FV",
            Number = "001003003016837",
            Date = new DateTime(2026, 10, 12, 0, 0, 0, DateTimeKind.Utc),
            IdentificationType = "04",
            Identification = "1760013210001",
            LegalName = "Servicio de Rentas Internas",
            Address = "Quito",
            AccessKey = "1210202601012345678900110010030030168371234567813",
            InvoiceDetails =
            [
                new InvoiceDetailDto
                {
                    Line = 1,
                    ProductCode = "4365",
                    ProductName = "Producto tarifa diferente de 0% ",
                    Quantity = 3m,
                    Unit = "UN",
                    UnitPrice = 2.6087m,
                    TaxCode = "4",
                    TaxIva = 15m,
                    ValueIva = 1.17m,
                    Discount = 0m,
                    TotalWithoutTax = 7.83m,
                    Total = 9m,
                    InvoiceDetailTaxes =
                    [
                        new TaxDto
                        {
                            TaxCode = "2",
                            TaxCodePercentage = "4",
                            TaxValue = 15m,
                            Base = 7.83m,
                            Value = 1.17m
                        }
                    ]
                },
                new InvoiceDetailDto
                {
                    Line = 2,
                    ProductCode = "4366",
                    ProductName = "Producto tarifa 0% ",
                    Quantity = 3m,
                    Unit = "UN",
                    UnitPrice = 3m,
                    TaxCode = "0",
                    TaxIva = 0m,
                    ValueIva = 0m,
                    Discount = 0m,
                    TotalWithoutTax = 9m,
                    Total = 9m,
                    InvoiceDetailTaxes =
                    [
                        new TaxDto
                        {
                            TaxCode = "2",
                            TaxCodePercentage = "0",
                            TaxValue = 0m,
                            Base = 9m,
                            Value = 0m
                        }
                    ]
                }
            ],
            Payments =
            [
                new PaymentDto { Code = "01", Total = 9m },
                new PaymentDto { Code = "20", Total = 9m }
            ],
            Informations =
            [
                new InformationDto { Name = "Dirección", Value = "Quito" },
                new InformationDto { Name = "Email", Value = "comprobantes@sri.gob.ec" }
            ]
        };
    }

    private async Task<HttpResponseMessage> SendTaxpayerAsync(TaxpayerDto taxpayer)
    {
        var taxpayerJson = JsonConvert.SerializeObject(taxpayer, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/taxpayer/rest/v1/taxpayer")
        {
            Content = new StringContent(taxpayerJson, Encoding.UTF8, "application/json")
        };
        request.Headers.Add("X-API-KEY", X_API_KEY);
        return await _client.SendAsync(request);
    }

    private async Task<HttpResponseMessage> SendInvoiceAsync(InvoiceDto invoice)
    {
        var invoiceJson = JsonConvert.SerializeObject(invoice, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/invoice/rest/v1/invoice/send")
        {
            Content = new StringContent(invoiceJson, Encoding.UTF8, "application/json")
        };
        request.Headers.Add("X-API-KEY", X_API_KEY);
        return await _client.SendAsync(request);
    }

    [Fact]
    public async Task GoodTaxpayerTest()
    {
        var taxpayer = InitData();

        Console.WriteLine($"GoodTaxpayerTest: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await SendTaxpayerAsync(taxpayer);
        Assert.True(apiResponse.IsSuccessStatusCode);

        var invoice = InitInvoiceData();

        var validAccessKey = invoice.AccessKey;
        invoice.AccessKey = invoice.AccessKey[..^1] + "4";

        Console.WriteLine($"BadAccessKeyInvoiceTest: {JsonConvert.SerializeObject(invoice)}");
        var invoiceResponse = await SendInvoiceAsync(invoice);
        Assert.False(invoiceResponse.IsSuccessStatusCode);
        Console.WriteLine($"BadAccessKeyInvoiceTest Response: {await invoiceResponse.Content.ReadAsStringAsync()}");

        invoice.AccessKey = validAccessKey;
        Console.WriteLine($"GoodInvoiceTest: {JsonConvert.SerializeObject(invoice)}");
        invoiceResponse = await SendInvoiceAsync(invoice);
        Assert.True(invoiceResponse.IsSuccessStatusCode);
    }
}