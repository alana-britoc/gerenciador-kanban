import { useMemo, useState } from 'react'
import { CalendarOff, ChevronLeft, ChevronRight } from 'lucide-react'
import { COLUNAS } from '../colunas'

const DIAS_DA_SEMANA = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb']

const doisDigitos = (n) => String(n).padStart(2, '0')
const paraIso = (data) => `${data.getFullYear()}-${doisDigitos(data.getMonth() + 1)}-${doisDigitos(data.getDate())}`
const colunaDe = (id) => COLUNAS.find((c) => c.id === id) ?? { nome: id, cor: 'var(--claro)' }
const ordemDaColuna = (id) => COLUNAS.findIndex((c) => c.id === id)

function primeiroDoMes(data) {
  return new Date(data.getFullYear(), data.getMonth(), 1)
}

function semanasDaGrade(mes) {
  const ultimoDia = new Date(mes.getFullYear(), mes.getMonth() + 1, 0).getDate()
  const semanas = Math.ceil((mes.getDay() + ultimoDia) / 7)
  return Array.from({ length: semanas }, (_, s) =>
    Array.from({ length: 7 }, (_, d) => new Date(mes.getFullYear(), mes.getMonth(), 1 - mes.getDay() + s * 7 + d)),
  )
}

function nomeDoMes(mes) {
  const texto = mes.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' })
  return texto.charAt(0).toUpperCase() + texto.slice(1)
}

function formatarDia(iso) {
  const [ano, mes, dia] = iso.split('-').map(Number)
  return new Date(ano, mes - 1, dia).toLocaleDateString('pt-BR', { weekday: 'short', day: '2-digit', month: '2-digit' })
}

const estaVencido = (cartao, hoje) => cartao.coluna !== 'FEITO' && cartao.extras.prazo < hoje

function Evento({ cartao, hoje }) {
  const coluna = colunaDe(cartao.coluna)
  const feito = cartao.coluna === 'FEITO'
  const vencido = estaVencido(cartao, hoje)
  const classes = ['evento']
  if (vencido) classes.push('evento-vencido')
  if (feito) classes.push('evento-feito')
  return (
    <li
      className={classes.join(' ')}
      style={{ '--cor-coluna': coluna.cor }}
      title={`${cartao.titulo} · ${coluna.nome}${vencido ? ' · prazo vencido' : ''}`}
    >
      <span className="evento-titulo">{cartao.titulo}</span>
    </li>
  )
}

export default function Calendario({ cartoes }) {
  const [hoje] = useState(() => paraIso(new Date()))
  const [mes, setMes] = useState(() => primeiroDoMes(new Date()))

  const porDia = useMemo(() => {
    const mapa = new Map()
    const comPrazo = cartoes
      .filter((c) => c.extras?.prazo)
      .sort((a, b) => ordemDaColuna(a.coluna) - ordemDaColuna(b.coluna) || a.titulo.localeCompare(b.titulo))
    for (const cartao of comPrazo) {
      const lista = mapa.get(cartao.extras.prazo) ?? []
      lista.push(cartao)
      mapa.set(cartao.extras.prazo, lista)
    }
    return mapa
  }, [cartoes])

  const semPrazo = cartoes.filter((c) => !c.extras?.prazo)
  const prefixoDoMes = paraIso(mes).slice(0, 7)
  const diasComCartoes = [...porDia.keys()].filter((dia) => dia.startsWith(prefixoDoMes)).sort()
  const vencidos = cartoes.filter((c) => c.extras?.prazo && estaVencido(c, hoje)).length

  const mudarMes = (delta) => setMes((atual) => new Date(atual.getFullYear(), atual.getMonth() + delta, 1))

  return (
    <div className="calendario-layout">
      <section className="calendario vidro" aria-label="Calendário de prazos">
        <header className="calendario-topo">
          <h2>{nomeDoMes(mes)}</h2>
          <div className="calendario-navegacao">
            <button type="button" className="botao" onClick={() => setMes(primeiroDoMes(new Date()))}>Hoje</button>
            <button type="button" className="botao-icone" aria-label="Mês anterior" onClick={() => mudarMes(-1)}>
              <ChevronLeft size={16} />
            </button>
            <button type="button" className="botao-icone" aria-label="Próximo mês" onClick={() => mudarMes(1)}>
              <ChevronRight size={16} />
            </button>
          </div>
        </header>

        <table className="grade">
          <caption className="so-leitor">Prazos de {nomeDoMes(mes)}</caption>
          <thead>
            <tr>
              {DIAS_DA_SEMANA.map((dia) => <th key={dia} scope="col">{dia}</th>)}
            </tr>
          </thead>
          <tbody>
            {semanasDaGrade(mes).map((semana) => (
              <tr key={paraIso(semana[0])}>
                {semana.map((data) => {
                  const iso = paraIso(data)
                  const doDia = porDia.get(iso) ?? []
                  const classes = []
                  if (data.getMonth() !== mes.getMonth()) classes.push('fora-do-mes')
                  if (iso === hoje) classes.push('hoje')
                  return (
                    <td key={iso} className={classes.join(' ') || undefined}>
                      <span className="grade-numero">{data.getDate()}</span>
                      {doDia.length > 0 && (
                        <>
                          <ul className="eventos">
                            {doDia.map((c) => <Evento key={c.id} cartao={c} hoje={hoje} />)}
                          </ul>
                          <span className="marcas" aria-hidden="true">
                            {doDia.map((c) => (
                              <span
                                key={c.id}
                                className="ponto"
                                style={{ '--cor-coluna': estaVencido(c, hoje) ? 'var(--perigo)' : colunaDe(c.coluna).cor }}
                              />
                            ))}
                          </span>
                        </>
                      )}
                    </td>
                  )
                })}
              </tr>
            ))}
          </tbody>
        </table>

        <div className="agenda">
          {diasComCartoes.length === 0 ? (
            <p className="agenda-vazia">Nenhum prazo neste mês</p>
          ) : (
            diasComCartoes.map((dia) => (
              <div key={dia} className="agenda-dia">
                <h3 className={dia === hoje ? 'agenda-hoje' : undefined}>{formatarDia(dia)}</h3>
                <ul className="eventos">
                  {porDia.get(dia).map((c) => <Evento key={c.id} cartao={c} hoje={hoje} />)}
                </ul>
              </div>
            ))
          )}
        </div>
      </section>

      <aside className="calendario-lateral">
        <section className="painel vidro" aria-label="Legenda">
          <h2>Legenda</h2>
          <ul className="legenda">
            {COLUNAS.map((c) => (
              <li key={c.id} style={{ '--cor-coluna': c.cor }}><span className="ponto" /> {c.nome}</li>
            ))}
            <li className="legenda-vencido"><span className="ponto" /> Prazo vencido{vencidos > 0 && ` (${vencidos})`}</li>
          </ul>
        </section>

        <section className="painel vidro" aria-label="Cartões sem prazo">
          <h2>
            <CalendarOff size={14} /> Sem prazo
            <span className="contador">{semPrazo.length}</span>
          </h2>
          {semPrazo.length === 0 ? (
            <p className="painel-vazio">Todos os cartões têm prazo</p>
          ) : (
            <ul className="sem-prazo">
              {semPrazo.map((c) => {
                const coluna = colunaDe(c.coluna)
                return (
                  <li key={c.id} style={{ '--cor-coluna': coluna.cor }}>
                    <span className="ponto" />
                    <span className="sem-prazo-titulo">{c.titulo}</span>
                    <span className="sem-prazo-coluna">{coluna.nome}</span>
                  </li>
                )
              })}
            </ul>
          )}
        </section>
      </aside>
    </div>
  )
}
