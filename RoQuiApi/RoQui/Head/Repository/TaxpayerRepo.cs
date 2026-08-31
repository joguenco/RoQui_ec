namespace RoQuiApi.RoQui.Head.Repository;

using RoQuiApi.Data;
using RoQuiApi.RoQui.Head.Model;

public class TaxpayerRepo : ITaxpayerRepo
{
    private readonly AppDbContext context;

    public TaxpayerRepo(AppDbContext context)
    {
        this.context = context;
    }

    public int CountTaxpayers()
    {
        return context.Taxpayers.Count();
    }

    public void CreateTaxpayer(Taxpayer taxpayer)
    {
        ArgumentNullException.ThrowIfNull(taxpayer);
        context.Taxpayers.Add(taxpayer);
    }

    public Taxpayer? GetTaxpayerByIdentification(string identification)
    {
        return context.Taxpayers.FirstOrDefault(t => t.Identification == identification);
    }

    public bool SaveChanges()
    {
        return context.SaveChanges() >= 0;
    }
}
