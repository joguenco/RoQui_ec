using RoQuiApi.RoQui.Head.Dto;
using RoQuiApi.RoQui.Head.Model;
using RoQuiApi.RoQui.Head.Repository;
using RoQuiApi.RoQui.Shared;

namespace RoQuiApi.RoQui.Head.Controller;

using AutoMapper;
using Microsoft.AspNetCore.Mvc;


[ApiController]
[Route("[controller]")]
public class TaxpayerController : ControllerBase
{

    private readonly ITaxpayerRepo _taxpayerRepo;
    private readonly IMapper _mapper;


    public TaxpayerController(ITaxpayerRepo taxpayerRepo, IMapper mapper)
    {
        this._taxpayerRepo = taxpayerRepo;
        this._mapper = mapper;
    }

    [HttpPost("rest/v1/taxpayer", Name = "CreateTaxpayer")]
    public ActionResult CreateTaxpayer(TaxpayerDto taxpayerBody)
    {
        if (_taxpayerRepo.CountTaxpayers() == 0)
        {
            var taxpayerModel = _mapper.Map<Taxpayer>(taxpayerBody);
            _taxpayerRepo.CreateTaxpayer(taxpayerModel);
            _taxpayerRepo.SaveChanges();

            return Ok(new MessageDto { Title = "Taxpayer created successfully" });
        }
        else if (_taxpayerRepo.CountTaxpayers() > 1)
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
        else if (_taxpayerRepo.CountTaxpayers() == 1)
        {
            var existingTaxpayer = _taxpayerRepo.GetTaxpayerByIdentification(taxpayerBody.Identification);
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
                _taxpayerRepo.DeleteEstablishments(existingTaxpayer.Establishments);
                var establishments = _mapper.Map<ICollection<Establishment>>(taxpayerBody.Establishments);
                existingTaxpayer.Establishments = establishments;
                existingTaxpayer.LegalName = taxpayerBody.LegalName;
                existingTaxpayer.ForcedAccounting = taxpayerBody.ForcedAccounting;
                existingTaxpayer.SpecialTaxpayer = taxpayerBody.SpecialTaxpayer;
                existingTaxpayer.RetentionAgent = taxpayerBody.RetentionAgent;
                existingTaxpayer.Rimpe = taxpayerBody.Rimpe;
                _taxpayerRepo.SaveChanges();

                return Ok(new MessageDto { Title = "Taxpayer updated successfully" });
            }
        }

        return StatusCode(StatusCodes.Status204NoContent, new MessageDto { Status = StatusCodes.Status204NoContent, Title = "Nothing to update" });
    }
}
