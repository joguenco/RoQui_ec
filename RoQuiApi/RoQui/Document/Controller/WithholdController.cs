namespace RoQuiApi.RoQui.Invoice.Controller;

using System.Net.Http.Json;
using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using RoQuiApi.RoQui.Document.Withhold.Dto;
using RoQuiApi.RoQui.Electronic.Repository;
using RoQuiApi.RoQui.Invoice.Model;
using RoQuiApi.RoQui.Invoice.Repository;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class WithholdController : ControllerBase
{
    private static readonly HttpClient HttpClient = new();

    private readonly IWithholdRepo _withholdRepo;

    private readonly IElectronicRepo _electronicRepo;
    private readonly IMapper _mapper;

    public WithholdController(IWithholdRepo withholdRepo, IElectronicRepo electronicRepo, IMapper mapper)
    {
        _withholdRepo = withholdRepo;
        _electronicRepo = electronicRepo;
        _mapper = mapper;
    }

    [HttpPost("rest/v1/withhold", Name = "CreateWithhold")]
    public ActionResult<MessageDto> CreateWithhold(WithholdDto withholdBody)
    {
        var existingWithhold = _withholdRepo.GetWithholdByCodeAndNumber(withholdBody.Code, withholdBody.Number);
        if (existingWithhold != null)
        {
            _withholdRepo.DeleteWithhold(existingWithhold);
        }

        var withholdModel = _mapper.Map<Model.Withhold>(withholdBody);
        var supportsModel = _mapper.Map<List<WithholdSupport>>(withholdBody.WithholdSupports);
        withholdModel.WithholdSupports = supportsModel;
        _withholdRepo.CreateWithhold(withholdModel);
        _withholdRepo.SaveChanges();

        var url = _electronicRepo.GetParameterByName("RoQui HTTP Server");
        if (!string.IsNullOrWhiteSpace(url?.Value))
        {
            var authorizeUrl = $"{url.Value.TrimEnd('/')}/roqui/v1/withhold/authorize";
            _ = AuthorizeWithhold(authorizeUrl, withholdBody.Code, withholdBody.Number);
        }

        return Ok(new MessageDto { Title = "Withhold created successfully" });
    }

    private static async Task AuthorizeWithhold(string authorizeUrl, string code, string number)
    {
        try
        {
            await Task.Delay(6000);
            using var content = JsonContent.Create(new { code, number });
            var response = await HttpClient.PostAsync(authorizeUrl, content);
            Console.WriteLine($"Authorize withhold {code}-{number}: {(int)response.StatusCode}");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Error authorizing withhold {code}-{number}: {ex.Message}");
        }
    }
}
