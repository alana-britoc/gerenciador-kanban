import { useEffect, useState } from 'react'
import { ArrowRight, CircleCheck, History, X } from 'lucide-react'
import { COLUNAS } from '../colunas'

const MAXIMO_NO_HISTORICO = 20
const relativo = new Intl.RelativeTimeFormat('pt-BR', { numeric: 'auto' })

const colunaDe = (id) => COLUNAS.find((c) => c.id === id) ?? { nome: id, cor: 'var(--claro)' }

function haQuantoTempo(horario, agora) {
  const segundos = Math.round((new Date(horario).getTime() - agora) / 1000)
  if (Math.abs(segundos) < 45) return 'agora'
  const minutos = Math.round(segundos / 60)
  if (Math.abs(minutos) < 60) return relativo.format(minutos, 'minute')
  const horas = Math.round(minutos / 60)
  if (Math.abs(horas) < 24) return relativo.format(horas, 'hour')
  return relativo.format(Math.round(horas / 24), 'day')
}

function Item({ cartaoId, existe, onAbrir, children }) {
  if (!existe(cartaoId)) return <div className="atividade-item">{children}</div>
  return (
    <button type="button" className="atividade-item" onClick={() => onAbrir(cartaoId)}>
      {children}
    </button>
  )
}

export default function PainelAtividade({ historico, notificacoes, existe, onAbrir, onFechar }) {
  const [agora, setAgora] = useState(() => Date.now())

  useEffect(() => {
    const relogio = setInterval(() => setAgora(Date.now()), 30_000)
    return () => clearInterval(relogio)
  }, [])

  const movimentos = historico.slice(-MAXIMO_NO_HISTORICO).reverse()
  const concluidos = [...notificacoes].reverse()

  return (
    <aside id="painel-atividade" className="painel-atividade vidro" aria-label="Atividade">
      <header className="painel-atividade-topo">
        <h2>Atividade</h2>
        <button type="button" className="botao-icone" aria-label="Fechar painel de atividade" onClick={onFechar}>
          <X size={16} />
        </button>
      </header>

      <section aria-labelledby="atividade-concluidos">
        <h3 id="atividade-concluidos">
          <CircleCheck size={14} /> Concluídos
          <span className="contador">{concluidos.length}</span>
        </h3>
        {concluidos.length === 0 ? (
          <p className="painel-vazio">Nenhum cartão chegou em Feito ainda</p>
        ) : (
          <ul className="atividade-lista">
            {concluidos.map((n, i) => (
              <li key={`${n.horario}-${i}`}>
                <Item cartaoId={n.cartaoId} existe={existe} onAbrir={onAbrir}>
                  <span className="atividade-icone-feito"><CircleCheck size={14} /></span>
                  <span className="atividade-texto">{n.mensagem}</span>
                  <time dateTime={n.horario}>{haQuantoTempo(n.horario, agora)}</time>
                </Item>
              </li>
            ))}
          </ul>
        )}
      </section>

      <section aria-labelledby="atividade-historico">
        <h3 id="atividade-historico">
          <History size={14} /> Histórico
        </h3>
        {movimentos.length === 0 ? (
          <p className="painel-vazio">Nenhum movimento ainda. Arraste um cartão para começar.</p>
        ) : (
          <ul className="atividade-lista">
            {movimentos.map((m, i) => {
              const origem = colunaDe(m.origem)
              const destino = colunaDe(m.destino)
              return (
                <li key={`${m.horario}-${i}`}>
                  <Item cartaoId={m.cartaoId} existe={existe} onAbrir={onAbrir}>
                    <span className="atividade-texto">
                      <strong>{m.titulo}</strong>
                      <span className="atividade-movimento">
                        <span className="ponto" style={{ '--cor-coluna': origem.cor }} /> {origem.nome}
                        <ArrowRight size={11} aria-label="para" />
                        <span className="ponto" style={{ '--cor-coluna': destino.cor }} /> {destino.nome}
                      </span>
                    </span>
                    <time dateTime={m.horario}>{haQuantoTempo(m.horario, agora)}</time>
                  </Item>
                </li>
              )
            })}
          </ul>
        )}
      </section>
    </aside>
  )
}
