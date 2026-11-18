import { useEffect, useRef, useState } from 'react'
import { useDraggable } from '@dnd-kit/core'
import { Ellipsis, Flag, PanelRightOpen, Tag, Trash2 } from 'lucide-react'
import { COLUNAS } from '../colunas'
import { TIPOS } from '../tipos'
import ExtrasCartao from './ExtrasCartao'
import NovoExtra from './NovoExtra'

const PRIORIDADES = { ALTA: 'Alta', MEDIA: 'Média', BAIXA: 'Baixa' }

const ELEMENTOS_INTERATIVOS = 'button, a, input, textarea, select, form, [role="menu"]'

function CorpoCartao({ cartao, onAbrir, children }) {
  const { nome, Icone, detalhe } = TIPOS[cartao.tipo]
  const prioridade = cartao.extras?.prioridade
  return (
    <>
      <div className="pilulas">
        <span className={`pilula pilula-tipo tipo-${cartao.tipo.toLowerCase()}`}>
          <Icone size={12} /> {nome}
        </span>
        {prioridade && (
          <span className={`pilula prioridade-${prioridade.toLowerCase()}`}>
            <Flag size={11} /> {PRIORIDADES[prioridade]}
          </span>
        )}
      </div>

      <h3>
        {onAbrir ? (
          <button type="button" className="cartao-titulo" onClick={() => onAbrir(cartao.id)}>
            {cartao.titulo}
          </button>
        ) : (
          cartao.titulo
        )}
      </h3>
      {cartao.descricao && <p className="cartao-descricao">{cartao.descricao}</p>}
      {cartao.detalhe && (
        <p className="cartao-detalhe">
          <strong>{detalhe}</strong>
          {cartao.detalhe}
        </p>
      )}

      {children}

      <ExtrasCartao id={cartao.id} extras={cartao.extras} coluna={cartao.coluna} />
    </>
  )
}

export function CartaoFlutuante({ cartao }) {
  return (
    <article className="cartao cartao-flutuante">
      <CorpoCartao cartao={cartao} />
      <span className="botao-menu" aria-hidden="true"><Ellipsis size={16} /></span>
    </article>
  )
}

export default function Cartao({ cartao, onAbrir, onMover, onRemover, onAdicionarExtra }) {
  const [menuAberto, setMenuAberto] = useState(false)
  const [editandoExtra, setEditandoExtra] = useState(false)
  const caixa = useRef(null)
  const { setNodeRef, listeners, isDragging } = useDraggable({
    id: cartao.id,
    disabled: menuAberto || editandoExtra,
  })

  useEffect(() => {
    if (!menuAberto) return
    const clicouFora = (e) => {
      if (!caixa.current?.contains(e.target)) setMenuAberto(false)
    }
    const apertouEsc = (e) => {
      if (e.key === 'Escape') setMenuAberto(false)
    }
    window.addEventListener('pointerdown', clicouFora)
    window.addEventListener('keydown', apertouEsc)
    return () => {
      window.removeEventListener('pointerdown', clicouFora)
      window.removeEventListener('keydown', apertouEsc)
    }
  }, [menuAberto])

  function escolher(fn) {
    setMenuAberto(false)
    fn()
  }

  const classes = ['cartao', 'cartao-arrastavel']
  if (menuAberto) classes.push('cartao-em-foco')
  if (isDragging) classes.push('cartao-origem')

  return (
    <article
      ref={setNodeRef}
      className={classes.join(' ')}
      {...listeners}
      onClick={(e) => {
        if (!e.target.closest(ELEMENTOS_INTERATIVOS)) onAbrir(cartao.id)
      }}
    >
      <CorpoCartao cartao={cartao} onAbrir={onAbrir}>
        {editandoExtra && (
          <NovoExtra
            onAdicionar={(tipo, valor) => onAdicionarExtra(cartao.id, tipo, valor)}
            onFechar={() => setEditandoExtra(false)}
          />
        )}
      </CorpoCartao>

      <div ref={caixa}>
        <button
          type="button"
          className="botao-menu"
          aria-label={`Ações de ${cartao.titulo}`}
          aria-haspopup="menu"
          aria-expanded={menuAberto}
          onClick={() => setMenuAberto((v) => !v)}
        >
          <Ellipsis size={16} />
        </button>
        {menuAberto && (
          <div role="menu" className="menu">
            <button type="button" role="menuitem" onClick={() => escolher(() => onAbrir(cartao.id))}>
              <PanelRightOpen size={14} /> Ver e editar
            </button>
            <hr />
            <p className="menu-titulo">Mover para</p>
            {COLUNAS.filter((col) => col.id !== cartao.coluna).map((col) => (
              <button
                key={col.id}
                type="button"
                role="menuitem"
                style={{ '--cor-coluna': col.cor }}
                onClick={() => escolher(() => onMover(cartao.id, col.id))}
              >
                <span className="ponto" /> {col.nome}
              </button>
            ))}
            <hr />
            <button type="button" role="menuitem" onClick={() => escolher(() => setEditandoExtra(true))}>
              <Tag size={14} /> Adicionar extra
            </button>
            <button
              type="button"
              role="menuitem"
              className="menu-perigo"
              onClick={() => escolher(() => onRemover(cartao.id))}
            >
              <Trash2 size={14} /> Remover cartão
            </button>
          </div>
        )}
      </div>
    </article>
  )
}
