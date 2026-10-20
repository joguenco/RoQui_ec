namespace RoQuiApi.Test;

using Newtonsoft.Json;

using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Shared;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("05_Liquidation")]
[TestCaseOrderer(
    ordererTypeName: "RoQuiApi.Test.MethodNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]
public class LiquidationUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;

    public LiquidationUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static LiquidationDto InitLiquidationData()
    {
        return new LiquidationDto
        {
            Code = "LQ",
            Number = "001002000000149",
            Date = new DateTime(2026, 10, 20, 0, 0, 0, DateTimeKind.Utc),
            IdentificationType = "05",
            Identification = "1009999901",
            LegalName = "GONZALO",
            Address = "Calle AGUSTIN 8",
            AccessKey = "2010202603012345678900110010020000001491234567811",
            LiquidationDetails =
            [
                new LiquidationDetailDto
                {
                    Line = 1,
                    ProductCode = "4370",
                    ProductName = "Producto tarifa diferente de 0%",
                    Quantity = 3m,
                    Unit = "UN",
                    UnitPrice = 1m,
                    TaxCode = "4",
                    TaxIva = 15m,
                    ValueIva = 0.45m,
                    Discount = 0m,
                    TotalWithoutTax = 3m,
                    Total = 3.45m,
                    LiquidationDetailTaxes = [new TaxDto
                        {
                            TaxCode = "2",
                            TaxCodePercentage = "4",
                            TaxValue = 15m,
                            Base = 3m,
                            Value = 0.45m
                        }]
                },
                new LiquidationDetailDto
                {
                    Line = 2,
                    ProductCode = "4371",
                    ProductName = "Producto tarifa 0%",
                    Quantity = 3m,
                    Unit = "UN",
                    UnitPrice = 1m,
                    TaxCode = "0",
                    TaxIva = 0m,
                    ValueIva = 0m,
                    Discount = 0m,
                    TotalWithoutTax = 3m,
                    Total = 3m,
                    LiquidationDetailTaxes = [new TaxDto
                        {
                            TaxCode = "2",
                            TaxCodePercentage = "0",
                            TaxValue = 0m,
                            Base = 3m,
                            Value = 0m
                        }]
                }
            ]
        };
    }

    private async Task<HttpResponseMessage> SendLiquidationAsync(LiquidationDto Liquidation)
    {
        var LiquidationJson = JsonConvert.SerializeObject(Liquidation, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/liquidation/rest/v1/send")
        {
            Content = new StringContent(LiquidationJson, Encoding.UTF8, "application/json")
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
    public async Task GoodLiquidationTest()
    {
        var Liquidation = InitLiquidationData();

        Console.WriteLine($"GoodLiquidationTest: {JsonConvert.SerializeObject(Liquidation)}");
        var LiquidationResponse = await SendLiquidationAsync(Liquidation);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await LiquidationResponse.Content.ReadAsStringAsync()
        );
        Assert.True(LiquidationResponse.IsSuccessStatusCode);
    }
}