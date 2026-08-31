using AutoMapper;
using RoQuiApi.RoQui.Head;
using RoQuiApi.RoQui.Head.Model;

namespace RoQuiApi.Profiles;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<Taxpayer, TaxpayerDto>();
        CreateMap<TaxpayerDto, Taxpayer>();
    }
}