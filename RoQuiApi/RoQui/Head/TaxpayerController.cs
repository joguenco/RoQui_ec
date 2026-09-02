namespace RoQuiApi.RoQui.Head;

using AutoMapper;
using Microsoft.AspNetCore.Mvc;
using RoQuiApi.RoQui.Head.Dto;
using RoQuiApi.RoQui.Head.Model;
using RoQuiApi.RoQui.Head.Repository;
using RoQuiApi.RoQui.Shared;

[ApiController]
[Route("[controller]")]
public class TaxpayerController : ControllerBase
{

    private readonly ITaxpayerRepo taxpayerRepo;
    private readonly IMapper mapper;


    public TaxpayerController(ITaxpayerRepo taxpayerRepo, IMapper mapper)
    {
        this.taxpayerRepo = taxpayerRepo;
        this.mapper = mapper;
    }

    [HttpPost("rest/v1/taxpayer", Name = "CreateTaxpayer")]
    public ActionResult CreateTaxpayer(TaxpayerDto taxpayerBody)
    {
        if (taxpayerRepo.CountTaxpayers() == 0)
        {
            var taxpayerModel = mapper.Map<Taxpayer>(taxpayerBody);
            taxpayerRepo.CreateTaxpayer(taxpayerModel);
            taxpayerRepo.SaveChanges();

            return Ok(new MessageDto { Title = "Taxpayer created successfully" });
        }
        else if (taxpayerRepo.CountTaxpayers() > 1)
        {

            return BadRequest(new MessageDto
            {
                Status = StatusCodes.Status400BadRequest,
                Title = "Various taxpayers in the database",
                Errors = new Error
                {
                    Message = ["There are various taxpayers in the database, please contact support"]
                }
            });
        }
        else if (taxpayerRepo.CountTaxpayers() == 1)
        {
            var existingTaxpayer = taxpayerRepo.GetTaxpayerByIdentification(taxpayerBody.Identification);
            if (existingTaxpayer == null)
            {
                return BadRequest(new MessageDto
                {
                    Status = StatusCodes.Status400BadRequest,
                    Title = "Taxpayer not found",
                    Errors = new Error
                    {
                        Message = ["The taxpayer with the provided identification does not exist in the database"]
                    }
                });
            }
            else
            {
                taxpayerRepo.DeleteEstablishments(existingTaxpayer.Establishments);
                var establishments = mapper.Map<ICollection<Establishment>>(taxpayerBody.Establishments);
                existingTaxpayer.Establishments = establishments;
                existingTaxpayer.LegalName = taxpayerBody.LegalName;
                existingTaxpayer.ForcedAccounting = taxpayerBody.ForcedAccounting;
                existingTaxpayer.SpecialTaxpayer = taxpayerBody.SpecialTaxpayer;
                existingTaxpayer.RetentionAgent = taxpayerBody.RetentionAgent;
                existingTaxpayer.Rimpe = taxpayerBody.Rimpe;
                taxpayerRepo.SaveChanges();

                return Ok(new MessageDto { Title = "Taxpayer updated successfully" });
            }
        }

        return StatusCode(StatusCodes.Status204NoContent, new MessageDto { Status = StatusCodes.Status204NoContent, Title = "Nothing to update" });
    }
}
