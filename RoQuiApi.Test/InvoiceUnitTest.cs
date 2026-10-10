namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Document.Invoice.Dto;
using RoQuiApi.RoQui.Shared;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("02_Invoice")]
[TestCaseOrderer(
    ordererTypeName: "RoQuiApi.Test.MethodNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]
public class InvoiceUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;

    public InvoiceUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
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
                    ProductName = "Producto tarifa diferente de 0%",
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
                    ProductName = "Producto tarifa 0%",
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

    private async Task<HttpResponseMessage> SendInvoiceAsync(InvoiceDto invoice)
    {
        var invoiceJson = JsonConvert.SerializeObject(invoice, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/invoice/rest/v1/invoice/send")
        {
            Content = new StringContent(invoiceJson, Encoding.UTF8, "application/json")
        };
        request.Headers.Add("X-API-KEY", DataTest.X_API_KEY);
        return await _client.SendAsync(request);
    }

    [Fact]
    public async Task ACreateTaxpayerTest()
    {
        var taxpayer = DataTest.InitData();

        Console.WriteLine($"GoodTaxpayerTest: {JsonConvert.SerializeObject(taxpayer)}");
        var apiResponse = await DataTest.SendTaxpayerAsync(_client, taxpayer);
        Assert.True(apiResponse.IsSuccessStatusCode);
    }

    [Fact]
    public async Task BBadAccessKeyModule11Test()
    {
        var invoice = InitInvoiceData();

        invoice.AccessKey = invoice.AccessKey[..^1] + "4";

        Console.WriteLine($"BadAccessKeyInvoiceTest: {JsonConvert.SerializeObject(invoice)}");
        var invoiceResponse = await SendInvoiceAsync(invoice);
        Assert.False(invoiceResponse.IsSuccessStatusCode);
        var message = JsonConvert.DeserializeObject<MessageDto>(
            await invoiceResponse.Content.ReadAsStringAsync()
        );
        Assert.Equal("Invalid data", message.Title);
        var errorMessage = message.Errors?.Message.FirstOrDefault();
        Console.WriteLine($"BadAccessKeyInvoiceTest Response: {errorMessage}");
        Assert.Equal("The access key check digit is not valid", errorMessage);
    }

    [Fact]
    public async Task CNegativeQuantityTest()
    {
        var invoiceWithNegative = InitInvoiceData();
        foreach (var invoiceDetail in invoiceWithNegative.InvoiceDetails)
        {
            invoiceDetail.Quantity = -1m;
        }
        var invoiceResponse = await SendInvoiceAsync(invoiceWithNegative);
        var message = JsonConvert.DeserializeObject<MessageDto>(
            await invoiceResponse.Content.ReadAsStringAsync()
        );
        var errorMessage = message.Errors?.Message.FirstOrDefault();
        Console.WriteLine($"NegativeQuantityInvoiceTest: {errorMessage}");
        Assert.Equal("Quantity cannot be negative", errorMessage);
    }

    [Fact]
    public async Task DAccessKeyWithOtherRucTest()
    {
        var invoice = InitInvoiceData();
        var accessKeyWithOtherRuc = "1210202601099999999900110010030030168371234567816";
        invoice.AccessKey = accessKeyWithOtherRuc;
        var invoiceResponse = await SendInvoiceAsync(invoice);
        var message = JsonConvert.DeserializeObject<MessageDto>(
            await invoiceResponse.Content.ReadAsStringAsync()
        );
        var errorMessage = message.Errors?.Message.FirstOrDefault();
        Console.WriteLine($"AccessKeyWithOtherRuc: {errorMessage}");
        Assert.Equal("The RUC 0999999999001 in the access key is not present", errorMessage);
    }

    [Fact]
    public async Task GoodInvoiceTest()
    {
        var invoice = InitInvoiceData();

        Console.WriteLine($"GoodInvoiceTest: {JsonConvert.SerializeObject(invoice)}");
        var invoiceResponse = await SendInvoiceAsync(invoice);
        Assert.True(invoiceResponse.IsSuccessStatusCode);
    }
}