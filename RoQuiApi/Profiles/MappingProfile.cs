using AutoMapper;
using RoQuiApi.RoQui.Head.Dto;
using RoQuiApi.RoQui.Head.Model;
using RoQuiApi.RoQui.Invoice.Dto;
using RoQuiApi.RoQui.Invoice.Model;

namespace RoQuiApi.Profiles;

public class MappingProfile : Profile
{
    public MappingProfile()
    {
        CreateMap<Taxpayer, TaxpayerDto>();
        CreateMap<TaxpayerDto, Taxpayer>();
        CreateMap<Establishment, EstablishmentDto>();
        CreateMap<EstablishmentDto, Establishment>();
        CreateMap<Invoice, InvoiceDto>();
        CreateMap<InvoiceDto, Invoice>();
    }
}