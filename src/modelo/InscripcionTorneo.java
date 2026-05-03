package modelo;

public class InscripcionTorneo {

    private Usuario usuario;
    private int     numeroCupos;
    private boolean usoCupoFanatico;

    public InscripcionTorneo(Usuario usuario, int numeroCupos, boolean usoCupoFanatico)
    {
        this.usuario         = usuario;
        this.numeroCupos     = numeroCupos;
        this.usoCupoFanatico = usoCupoFanatico;
    }

    // ── Getters y setters ─────────────────────────────────────────────────

    public Usuario getUsuario()                      { return usuario; }
    public void    setUsuario(Usuario usuario)        { this.usuario = usuario; }

    public int  getNumeroCupos()                     { return numeroCupos; }
    public void setNumeroCupos(int numeroCupos)      { this.numeroCupos = numeroCupos; }

    public boolean isUsoCupoFanatico()               { return usoCupoFanatico; }
    public void    setUsoCupoFanatico(boolean uso)   { this.usoCupoFanatico = uso; }
}