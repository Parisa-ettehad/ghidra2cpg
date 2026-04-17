(module
  (type $alloc_t (func (result i32)))
  (type $free_t  (func (param i32)))
  (type $use_t   (func (param i32)))
  (type $bad_t   (func))

  (import "env" "malloc" (func $malloc (type $alloc_t)))
  (import "env" "free"   (func $free   (type $free_t)))
  (import "env" "use"    (func $use    (type $use_t)))

  (func $bad (type $bad_t)
    (local $ptr i32)

    call $malloc
    local.set $ptr

    local.get $ptr
    call $free

    local.get $ptr
    call $use
  )
)

