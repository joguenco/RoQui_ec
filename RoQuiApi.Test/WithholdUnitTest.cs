namespace RoQuiApi.Test;

using Newtonsoft.Json;

using RoQuiApi.RoQui.Document.Withhold.Dto;
using RoQuiApi.RoQui.Shared;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("06_Withhold")]
[TestCaseOrderer(
    ordererTypeName: "RoQuiApi.Test.MethodNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]
public class WithholdUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;

    public WithholdUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static WithholdDto InitWithholdData()
    {
        return new WithholdDto
        {
            Code = "RT",
            Number = "001002000014754",
            Date = new DateTime(2026, 10, 20, 0, 0, 0, DateTimeKind.Utc),
            IdentificationType = "04",
            Identification = "1760013210001",
            LegalName = "Servicio de Rentas Internas",
            AccessKey = "2010202607012345678900110010020000147541234567814",
            FiscalPeriod = "10/2026",
            Related = "NO",
            WithholdSupports =
            [
                new WithholdSupportDto
                {
                    CodeSupport = "02",
                    CodeDocumentSupport = "01",
                    NumberDocumentSupport = "001001000000123",
                    DateDocumentSupport = new DateTime(2026, 10, 19, 0, 0, 0, DateTimeKind.Utc),
                    AuthorizationDocumentSupport = "1234567890",
                    TotalWithoutTaxes = 100m,
                    Total = 115m,
                    WithholdDetails =
                    [
                        new WithholdDetailDto
                        {
                            Line = 1,
                            TaxCode = "2",
                            WithholdCode = "3",
                            BaseValue = 15m,
                            Percentage = 100m,
                            WithholdedValue = 15m
                        },
                        new WithholdDetailDto
                        {
                            Line = 2,
                            TaxCode = "1",
                            WithholdCode = "303",
                            BaseValue = 100m,
                            Percentage = 10m,
                            WithholdedValue = 10m
                        }
                    ],
                    WithholdDocumentTaxes =
                    [
                        new WithholdDocumentTaxDto
                        {
                            TaxCode = "2",
                            PercentageCode = "4",
                            TaxBase = 100m,
                            TaxIva = 15m,
                            Value = 15m
                        }
                    ]
                }
            ]
        };
    }

    private async Task<HttpResponseMessage> SendWithholdAsync(WithholdDto withhold)
    {
        var withholdJson = JsonConvert.SerializeObject(withhold, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/withhold/rest/v1/send")
        {
            Content = new StringContent(withholdJson, Encoding.UTF8, "application/json")
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
    public async Task GoodWithholdTest()
    {
        var withhold = InitWithholdData();

        Console.WriteLine($"GoodWithholdTest: {JsonConvert.SerializeObject(withhold)}");
        var withholdResponse = await SendWithholdAsync(withhold);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await withholdResponse.Content.ReadAsStringAsync()
        );
        Assert.True(withholdResponse.IsSuccessStatusCode);
    }
}