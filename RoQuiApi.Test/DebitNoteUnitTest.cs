namespace RoQuiApi.Test;

using Newtonsoft.Json;

using RoQuiApi.RoQui.Document.Dto;
using RoQuiApi.RoQui.Shared;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Xunit;

[Collection("04_DebitNote")]
[TestCaseOrderer(
    ordererTypeName: "RoQuiApi.Test.MethodNameOrderer",
    ordererAssemblyName: "RoQuiApi.Test")]
public class DebitNoteUnitTest : IClassFixture<CustomWebApplicationFactory<Program>>
{

    private readonly HttpClient _client;

    public DebitNoteUnitTest(CustomWebApplicationFactory<Program> factory)
    {
        _client = factory.CreateClient();
    }

    public static DebitNoteDto InitDebitNoteData()
    {
        return new DebitNoteDto
        {
            Code = "ND",
            Number = "001002000000001",
            Date = new DateTime(2026, 10, 20, 0, 0, 0, DateTimeKind.Utc),
            IdentificationType = "04",
            Identification = "1760013210001",
            LegalName = "Servicio de Rentas Internas",
            AccessKey = "2010202605012345678900110010020000000011234567817",
            ModifiedDocumentType = "01",
            ModifiedDocument = "001-003-003016875",
            ModifiedDate = new DateTime(2026, 10, 19, 16, 13, 2, DateTimeKind.Utc),
            TotalWithoutTaxes = 86.09m,
            DebitNoteDetails =
    [
        new DebitNoteDetailDto
                {
                    Reason = "Multa",
                    Value = 86.09m
                }
    ],
            DebitNoteTaxes = [
                new TaxDto
                        {
                            TaxCode = "2",
                            TaxCodePercentage = "4",
                            TaxValue = 15m,
                            Base = 86.09m,
                            Value = 12.91m
                        }
            ],
            Payments =
    [
        new PaymentDto
                {
                    Code = "20",
                    Total = 99m,
                    Deadline = 8m,
                    UnitTime = "Día"
                }
    ]
        };
    }

    private async Task<HttpResponseMessage> SendDebitNoteAsync(DebitNoteDto debitNote)
    {
        var debitNoteJson = JsonConvert.SerializeObject(debitNote, Formatting.Indented);
        using var request = new HttpRequestMessage(HttpMethod.Post, "/debitnote/rest/v1/debitnote/send")
        {
            Content = new StringContent(debitNoteJson, Encoding.UTF8, "application/json")
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
    public async Task BBadCodeDebitNoteTest()
    {
        var debitNote = InitDebitNoteData();
        debitNote.AccessKey = "2010202601012345678900110010020000000011234567811";

        Console.WriteLine($"BadCodeDebitNoteTest: {JsonConvert.SerializeObject(debitNote)}");
        var debitNoteResponse = await SendDebitNoteAsync(debitNote);
        Assert.False(debitNoteResponse.IsSuccessStatusCode);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await debitNoteResponse.Content.ReadAsStringAsync()
        );
        var errorMessage = message.Errors?.Message.FirstOrDefault();
        Assert.Equal("The code ND must be 05 in the accessKey", errorMessage);
    }

    [Fact]
    public async Task GoodDebitNoteTest()
    {
        var debitNote = InitDebitNoteData();

        Console.WriteLine($"GoodDebitNoteTest: {JsonConvert.SerializeObject(debitNote)}");
        var debitNoteResponse = await SendDebitNoteAsync(debitNote);

        var message = JsonConvert.DeserializeObject<MessageDto>(
            await debitNoteResponse.Content.ReadAsStringAsync()
        );
        Assert.True(debitNoteResponse.IsSuccessStatusCode);
    }
}