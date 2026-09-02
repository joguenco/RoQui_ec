using RoQuiApi.RoQui.Head.Model;

namespace RoQuiApi.RoQui.Head.Repository;

public interface ITaxpayerRepo
{
    public Taxpayer? GetTaxpayerByIdentification(string identification);

    void DeleteEstablishments(ICollection<Establishment> establishments);

    void CreateTaxpayer(Model.Taxpayer taxpayer);

    int CountTaxpayers();

    bool SaveChanges();
}