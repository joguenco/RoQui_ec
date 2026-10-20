namespace RoQuiApi.Test;

using Newtonsoft.Json;
using RoQuiApi.RoQui.CreditNote.Dto;
using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Shared;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("03_CreditNote")]
[TestCaseOrderer(
    ordererTypeName: "RoQuiApi.Test.MethodNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]
public class CreditNoteUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;

    public CreditNoteUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static CreditNoteDto InitCreditNoteData()
    {
        return new CreditNoteDto
        {
            Code = "DV",
            Number = "001002000001812",
            Date = new DateTime(2026, 10, 19, 0, 0, 0, DateTimeKind.Utc),
            IdentificationType = "04",
            Identification = "1760013210001",
            LegalName = "Servicio de Rentas Internas",
            AccessKey = "1910202604012345678900110010020000018121234567815",
            ModifiedDocumentType = "01",
            ModifiedDocument = "001-003-003016875",
            ModifiedDate = new DateTime(2026, 10, 19, 0, 0, 0, DateTimeKind.Utc),
            Reason = "Cliente devuelve",
            TotalWithoutTaxes = 16.83m,
            ModifiedTotal = 18m,
            CreditNoteDetails =
            [
                new CreditNoteDetailDto
                {
                    Line = 1,
                    ProductCode = "4371",
                    ProductName = "Producto tarifa 0%",
                    Quantity = 3m,
                    UnitPrice = 3m,
                    TaxCode = "0",
                    TaxIva = 0m,
                    ValueIva = 0m,
                    Discount = 0m,
                    TotalWithoutTax = 9m,
                    Total = 9m,
                    CreditNoteDetailTaxes =
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
                },
                new CreditNoteDetailDto
                {
                    Line = 2,
                    ProductCode = "4370",
                    ProductName = "Producto tarifa diferente de 0%",
                    Quantity = 3m,
                    UnitPrice = 2.61m,
                    TaxCode = "4",
                    TaxIva = 15m,
                    ValueIva = 1.17m,
                    Discount = 0m,
                    TotalWithoutTax = 7.83m,
                    Total = 9m,
                    CreditNoteDetailTaxes =
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
                }
            ]
        };
    }

    private async Task<HttpResponseMessage> SendCreditNoteAsync(CreditNoteDto creditNote)
    {
        var creditNoteJson = JsonConvert.SerializeObject(creditNote, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/creditnote/rest/v1/creditnote/send")
        {
            Content = new StringContent(creditNoteJson, Encoding.UTF8, "application/json")
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
    public async Task BCreditNoteWithoutModifiedTest()
    {
        var creditNote = InitCreditNoteData();
        creditNote.ModifiedDocument = "";

        Console.WriteLine($"CreditNoteWithoutModifiedTest: {JsonConvert.SerializeObject(creditNote)}");
        var creditNoteResponse = await SendCreditNoteAsync(creditNote);
        Assert.False(creditNoteResponse.IsSuccessStatusCode);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await creditNoteResponse.Content.ReadAsStringAsync()
        );
        var errorMessage = message.Errors?.Message.FirstOrDefault();
        Assert.Equal("The ModifiedDocument field is required.", errorMessage);
    }

    [Fact]
    public async Task GoodCreditNoteTest()
    {
        var creditNote = InitCreditNoteData();

        Console.WriteLine($"GoodCreditNoteTest: {JsonConvert.SerializeObject(creditNote)}");
        var creditNoteResponse = await SendCreditNoteAsync(creditNote);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await creditNoteResponse.Content.ReadAsStringAsync()
        );
        Assert.True(creditNoteResponse.IsSuccessStatusCode);
    }
}