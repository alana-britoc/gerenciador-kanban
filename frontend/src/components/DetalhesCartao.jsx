import { useEffect, useRef, useState } from 'react'
import { CalendarDays, Flag, Plus, RotateCcw, Tag, Trash2, X } from 'lucide-react'
import { COLUNAS } from '../colunas'
import { TIPOS } from '../tipos'
import NovoExtra from './NovoExtra'
import Seletor from './Seletor'

const PRIORIDADES = { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' }

function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano}`
}

export default function DetalhesCartao({ cartao, onFechar, onSalvar, onMover, onAdicionarExtra, onRemoverExtra, onRemover }) {
  const dialogo = useRef(null)
  const [titulo, setTitulo] = useState(cartao.titulo)
  const [descricao, setDescricao] = useState(cartao.descricao ?? '')
  const [detalhe, setDetalhe] = useState(cartao.detalhe)
  const [salvando, setSalvando] = useState(false)
  const [salvo, setSalvo] = useState(false)
  const [adicionandoExtra, setAdicionandoExtra] = useState(false)
  const [confirmandoRemocao, setConfirmandoRemocao] = useState(false)
  const [hoje] = useState(() => new Date().toLocaleDateString('sv-SE'))

  useEffect(() => {
    const elemento = dialogo.current
    if (!elemento.open) elemento.showModal()
  }, [])

  const tipo = TIPOS[cartao.tipo]
  const { Icone } = tipo
  const { prazo, prioridade, etiquetas = [] } = cartao.extras ?? {}
  const vencido = prazo && cartao.coluna !== 'FEITO' && prazo < hoje
  const alterado =
    titulo.trim() !== cartao.titulo || descricao.trim() !== (cartao.descricao ?? '') || detalhe.trim() !== cartao.detalhe

  function fechar() {
    dialogo.current?.close()
  }

  function desfazer() {
    setTitulo(cartao.titulo)
    setDescricao(cartao.descricao ?? '')
    setDetalhe(cartao.detalhe)
  }

  async function salvar(e) {
    e.preventDefault()
    if (!titulo.trim() || !detalhe.trim()) return
    setSalvando(true)
    const ok = await onSalvar(cartao.id, { titulo: titulo.trim(), descricao: descricao.trim(), detalhe: detalhe.trim() })
    setSalvando(false)
    if (ok) {
      setTitulo(titulo.trim())
      setDescricao(descricao.trim())
      setDetalhe(detalhe.trim())
      setSalvo(true)
    }
  }

  async function remover() {
    if (await onRemover(cartao.id)) fechar()
  }

  const prioridadeRemovivel = cartao.tipo !== 'BUG' || prioridade !== 'ALTA'

  return (
    <dialog
      ref={dialogo}
      className="detalhes"
      aria-labelledby="detalhes-titulo"
      onClose={onFechar}
      onClick={(e) => {
        if (e.target === dialogo.current) fechar()
      }}
    >
      <div className="detalhes-conteudo">
        <header className="detalhes-topo">
          <div className="pilulas">
            <span className={`pilula pilula-tipo tipo-${cartao.tipo.toLowerCase()}`}>
              <Icone size={12} /> {tipo.nome}
            </span>
            {prioridade && (
              <span className={`pilula prioridade-${prioridade.toLowerCase()}`}>
                <Flag size={11} /> {PRIORIDADES[prioridade]}
              </span>
            )}
            <span className="detalhes-id">#{cartao.id}</span>
          </div>
          <button type="button" className="botao-icone" aria-label="Fechar detalhes" onClick={fechar}>
            <X size={16} />
          </button>
        </header>

        <form className="detalhes-form" onSubmit={salvar}>
          <h2 id="detalhes-titulo" className="so-leitor">Detalhes de {cartao.titulo}</h2>
          <label className="rotulo">
            Título
            <input
              className="campo campo-titulo"
              value={titulo}
              onChange={(e) => {
                setTitulo(e.target.value)
                setSalvo(false)
              }}
              maxLength={255}
              required
            />
          </label>
          <label className="rotulo">
            Descrição
            <textarea
              className="campo"
              rows={3}
              value={descricao}
              onChange={(e) => {
                setDescricao(e.target.value)
                setSalvo(false)
              }}
              placeholder="Sem descrição"
            />
          </label>
          <label className="rotulo">
            {tipo.detalhe}
            <textarea
              className="campo"
              rows={3}
              value={detalhe}
              onChange={(e) => {
                setDetalhe(e.target.value)
                setSalvo(false)
              }}
              required
            />
          </label>
          <div className="form-acoes">
            {salvo && !alterado && <span className="detalhes-salvo" role="status">Alterações salvas</span>}
            {alterado && <button type="button" className="botao" onClick={desfazer}>Desfazer</button>}
            <button type="submit" className="botao botao-primario" disabled={!alterado || salvando}>
              Salvar alterações
            </button>
          </div>
        </form>

        <section className="detalhes-secao" aria-labelledby="detalhes-coluna">
          <h3 id="detalhes-coluna">Coluna</h3>
          <Seletor
            rotulo="Coluna do cartão"
            opcoes={COLUNAS.map((c) => ({ valor: c.id, nome: c.nome }))}
            valor={cartao.coluna}
            onMudar={(coluna) => coluna !== cartao.coluna && onMover(cartao.id, coluna)}
          />
        </section>

        <section className="detalhes-secao" aria-labelledby="detalhes-extras">
          <h3 id="detalhes-extras">Extras</h3>
          <ul className="detalhes-extras">
            <li>
              <span className="detalhes-extra-nome"><CalendarDays size={14} /> Prazo</span>
              {prazo ? (
                <>
                  <span className={vencido ? 'prazo-vencido' : undefined}>
                    {formatarData(prazo)}{vencido && ' · vencido'}
                  </span>
                  <button type="button" className="botao-texto" onClick={() => onRemoverExtra(cartao.id, 'PRAZO')}>
                    Remover
                  </button>
                </>
              ) : (
                <span className="detalhes-vazio">Sem prazo</span>
              )}
            </li>
            <li>
              <span className="detalhes-extra-nome"><Flag size={14} /> Prioridade</span>
              {prioridade ? (
                <>
                  <span className={`pilula prioridade-${prioridade.toLowerCase()}`}>{PRIORIDADES[prioridade]}</span>
                  {prioridadeRemovivel ? (
                    <button
                      type="button"
                      className="botao-texto"
                      onClick={() => onRemoverExtra(cartao.id, 'PRIORIDADE')}
                    >
                      {cartao.tipo === 'BUG' ? <><RotateCcw size={12} /> Voltar para Alta</> : 'Remover'}
                    </button>
                  ) : (
                    <span className="detalhes-vazio">padrão dos bugs</span>
                  )}
                </>
              ) : (
                <span className="detalhes-vazio">Sem prioridade</span>
              )}
            </li>
            <li>
              <span className="detalhes-extra-nome"><Tag size={14} /> Etiquetas</span>
              {etiquetas.length > 0 ? (
                <span className="detalhes-etiquetas">
                  {etiquetas.map((etiqueta) => (
                    <span key={etiqueta} className="etiqueta">
                      {etiqueta}
                      <button
                        type="button"
                        aria-label={`Remover etiqueta ${etiqueta}`}
                        onClick={() => onRemoverExtra(cartao.id, 'ETIQUETA', etiqueta)}
                      >
                        <X size={11} />
                      </button>
                    </span>
                  ))}
                </span>
              ) : (
                <span className="detalhes-vazio">Sem etiquetas</span>
              )}
            </li>
          </ul>
          {adicionandoExtra ? (
            <NovoExtra
              onAdicionar={(tipoExtra, valor) => onAdicionarExtra(cartao.id, tipoExtra, valor)}
              onFechar={() => setAdicionandoExtra(false)}
            />
          ) : (
            <button type="button" className="botao botao-com-icone" onClick={() => setAdicionandoExtra(true)}>
              <Plus size={14} /> Adicionar extra
            </button>
          )}
        </section>

        <footer className="detalhes-rodape">
          {confirmandoRemocao ? (
            <>
              <span className="detalhes-confirmar">Remover este cartão?</span>
              <button type="button" className="botao" onClick={() => setConfirmandoRemocao(false)}>Cancelar</button>
              <button type="button" className="botao botao-perigo" onClick={remover}>Remover</button>
            </>
          ) : (
            <button type="button" className="botao-texto botao-texto-perigo" onClick={() => setConfirmandoRemocao(true)}>
              <Trash2 size={14} /> Remover cartão
            </button>
          )}
        </footer>
      </div>
    </dialog>
  )
}
